import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILikeEntry, LikeEntry } from '../like-entry.model';
import { LikeEntryService } from '../service/like-entry.service';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { IPaper } from 'app/entities/paper/paper.model';
import { PaperService } from 'app/entities/paper/service/paper.service';

@Component({
  selector: 'jhi-like-entry-update',
  templateUrl: './like-entry-update.component.html',
})
export class LikeEntryUpdateComponent implements OnInit {
  isSaving = false;

  appUsersSharedCollection: IAppUser[] = [];
  papersSharedCollection: IPaper[] = [];

  editForm = this.fb.group({
    id: [],
    createDate: [],
    appUsers: [],
    papers: [],
  });

  constructor(
    protected likeEntryService: LikeEntryService,
    protected appUserService: AppUserService,
    protected paperService: PaperService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ likeEntry }) => {
      this.updateForm(likeEntry);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const likeEntry = this.createFromForm();
    if (likeEntry.id !== undefined) {
      this.subscribeToSaveResponse(this.likeEntryService.update(likeEntry));
    } else {
      this.subscribeToSaveResponse(this.likeEntryService.create(likeEntry));
    }
  }

  trackAppUserById(index: number, item: IAppUser): number {
    return item.id!;
  }

  trackPaperById(index: number, item: IPaper): number {
    return item.id!;
  }

  getSelectedAppUser(option: IAppUser, selectedVals?: IAppUser[]): IAppUser {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedPaper(option: IPaper, selectedVals?: IPaper[]): IPaper {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILikeEntry>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(likeEntry: ILikeEntry): void {
    this.editForm.patchValue({
      id: likeEntry.id,
      createDate: likeEntry.createDate,
      appUsers: likeEntry.appUsers,
      papers: likeEntry.papers,
    });

    this.appUsersSharedCollection = this.appUserService.addAppUserToCollectionIfMissing(
      this.appUsersSharedCollection,
      ...(likeEntry.appUsers ?? [])
    );
    this.papersSharedCollection = this.paperService.addPaperToCollectionIfMissing(this.papersSharedCollection, ...(likeEntry.papers ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.appUserService
      .query()
      .pipe(map((res: HttpResponse<IAppUser[]>) => res.body ?? []))
      .pipe(
        map((appUsers: IAppUser[]) =>
          this.appUserService.addAppUserToCollectionIfMissing(appUsers, ...(this.editForm.get('appUsers')!.value ?? []))
        )
      )
      .subscribe((appUsers: IAppUser[]) => (this.appUsersSharedCollection = appUsers));

    this.paperService
      .query()
      .pipe(map((res: HttpResponse<IPaper[]>) => res.body ?? []))
      .pipe(
        map((papers: IPaper[]) => this.paperService.addPaperToCollectionIfMissing(papers, ...(this.editForm.get('papers')!.value ?? [])))
      )
      .subscribe((papers: IPaper[]) => (this.papersSharedCollection = papers));
  }

  protected createFromForm(): ILikeEntry {
    return {
      ...new LikeEntry(),
      id: this.editForm.get(['id'])!.value,
      createDate: this.editForm.get(['createDate'])!.value,
      appUsers: this.editForm.get(['appUsers'])!.value,
      papers: this.editForm.get(['papers'])!.value,
    };
  }
}
