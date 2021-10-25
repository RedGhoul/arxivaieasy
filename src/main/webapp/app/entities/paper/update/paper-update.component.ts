import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPaper, Paper } from '../paper.model';
import { PaperService } from '../service/paper.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

@Component({
  selector: 'jhi-paper-update',
  templateUrl: './paper-update.component.html',
})
export class PaperUpdateComponent implements OnInit {
  isSaving = false;

  authorsSharedCollection: IAuthor[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    abstractText: [null, [Validators.required]],
    submitedDate: [],
    createdDate: [],
    pdfLink: [null, [Validators.required]],
    baseLink: [null, [Validators.required]],
    authors: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected paperService: PaperService,
    protected authorService: AuthorService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paper }) => {
      this.updateForm(paper);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('arXivAiApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const paper = this.createFromForm();
    if (paper.id !== undefined) {
      this.subscribeToSaveResponse(this.paperService.update(paper));
    } else {
      this.subscribeToSaveResponse(this.paperService.create(paper));
    }
  }

  trackAuthorById(index: number, item: IAuthor): number {
    return item.id!;
  }

  getSelectedAuthor(option: IAuthor, selectedVals?: IAuthor[]): IAuthor {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaper>>): void {
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

  protected updateForm(paper: IPaper): void {
    this.editForm.patchValue({
      id: paper.id,
      title: paper.title,
      abstractText: paper.abstractText,
      submitedDate: paper.submitedDate,
      createdDate: paper.createdDate,
      pdfLink: paper.pdfLink,
      baseLink: paper.baseLink,
      authors: paper.authors,
    });

    this.authorsSharedCollection = this.authorService.addAuthorToCollectionIfMissing(
      this.authorsSharedCollection,
      ...(paper.authors ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.authorService
      .query()
      .pipe(map((res: HttpResponse<IAuthor[]>) => res.body ?? []))
      .pipe(
        map((authors: IAuthor[]) =>
          this.authorService.addAuthorToCollectionIfMissing(authors, ...(this.editForm.get('authors')!.value ?? []))
        )
      )
      .subscribe((authors: IAuthor[]) => (this.authorsSharedCollection = authors));
  }

  protected createFromForm(): IPaper {
    return {
      ...new Paper(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      abstractText: this.editForm.get(['abstractText'])!.value,
      submitedDate: this.editForm.get(['submitedDate'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value,
      pdfLink: this.editForm.get(['pdfLink'])!.value,
      baseLink: this.editForm.get(['baseLink'])!.value,
      authors: this.editForm.get(['authors'])!.value,
    };
  }
}
