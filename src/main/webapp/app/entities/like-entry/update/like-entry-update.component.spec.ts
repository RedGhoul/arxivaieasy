jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LikeEntryService } from '../service/like-entry.service';
import { ILikeEntry, LikeEntry } from '../like-entry.model';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { AppUserService } from 'app/entities/app-user/service/app-user.service';
import { IPaper } from 'app/entities/paper/paper.model';
import { PaperService } from 'app/entities/paper/service/paper.service';

import { LikeEntryUpdateComponent } from './like-entry-update.component';

describe('Component Tests', () => {
  describe('LikeEntry Management Update Component', () => {
    let comp: LikeEntryUpdateComponent;
    let fixture: ComponentFixture<LikeEntryUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let likeEntryService: LikeEntryService;
    let appUserService: AppUserService;
    let paperService: PaperService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LikeEntryUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(LikeEntryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LikeEntryUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      likeEntryService = TestBed.inject(LikeEntryService);
      appUserService = TestBed.inject(AppUserService);
      paperService = TestBed.inject(PaperService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call AppUser query and add missing value', () => {
        const likeEntry: ILikeEntry = { id: 456 };
        const appUsers: IAppUser[] = [{ id: 50401 }];
        likeEntry.appUsers = appUsers;

        const appUserCollection: IAppUser[] = [{ id: 53994 }];
        jest.spyOn(appUserService, 'query').mockReturnValue(of(new HttpResponse({ body: appUserCollection })));
        const additionalAppUsers = [...appUsers];
        const expectedCollection: IAppUser[] = [...additionalAppUsers, ...appUserCollection];
        jest.spyOn(appUserService, 'addAppUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ likeEntry });
        comp.ngOnInit();

        expect(appUserService.query).toHaveBeenCalled();
        expect(appUserService.addAppUserToCollectionIfMissing).toHaveBeenCalledWith(appUserCollection, ...additionalAppUsers);
        expect(comp.appUsersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Paper query and add missing value', () => {
        const likeEntry: ILikeEntry = { id: 456 };
        const papers: IPaper[] = [{ id: 88325 }];
        likeEntry.papers = papers;

        const paperCollection: IPaper[] = [{ id: 66219 }];
        jest.spyOn(paperService, 'query').mockReturnValue(of(new HttpResponse({ body: paperCollection })));
        const additionalPapers = [...papers];
        const expectedCollection: IPaper[] = [...additionalPapers, ...paperCollection];
        jest.spyOn(paperService, 'addPaperToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ likeEntry });
        comp.ngOnInit();

        expect(paperService.query).toHaveBeenCalled();
        expect(paperService.addPaperToCollectionIfMissing).toHaveBeenCalledWith(paperCollection, ...additionalPapers);
        expect(comp.papersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const likeEntry: ILikeEntry = { id: 456 };
        const appUsers: IAppUser = { id: 13261 };
        likeEntry.appUsers = [appUsers];
        const papers: IPaper = { id: 90467 };
        likeEntry.papers = [papers];

        activatedRoute.data = of({ likeEntry });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(likeEntry));
        expect(comp.appUsersSharedCollection).toContain(appUsers);
        expect(comp.papersSharedCollection).toContain(papers);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<LikeEntry>>();
        const likeEntry = { id: 123 };
        jest.spyOn(likeEntryService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ likeEntry });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: likeEntry }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(likeEntryService.update).toHaveBeenCalledWith(likeEntry);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<LikeEntry>>();
        const likeEntry = new LikeEntry();
        jest.spyOn(likeEntryService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ likeEntry });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: likeEntry }));
        saveSubject.complete();

        // THEN
        expect(likeEntryService.create).toHaveBeenCalledWith(likeEntry);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<LikeEntry>>();
        const likeEntry = { id: 123 };
        jest.spyOn(likeEntryService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ likeEntry });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(likeEntryService.update).toHaveBeenCalledWith(likeEntry);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackAppUserById', () => {
        it('Should return tracked AppUser primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackAppUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPaperById', () => {
        it('Should return tracked Paper primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPaperById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedAppUser', () => {
        it('Should return option if no AppUser is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedAppUser(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected AppUser for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedAppUser(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this AppUser is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedAppUser(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });

      describe('getSelectedPaper', () => {
        it('Should return option if no Paper is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedPaper(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Paper for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedPaper(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Paper is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedPaper(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
