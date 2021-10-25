jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PaperService } from '../service/paper.service';
import { IPaper, Paper } from '../paper.model';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

import { PaperUpdateComponent } from './paper-update.component';

describe('Component Tests', () => {
  describe('Paper Management Update Component', () => {
    let comp: PaperUpdateComponent;
    let fixture: ComponentFixture<PaperUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let paperService: PaperService;
    let authorService: AuthorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PaperUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PaperUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaperUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      paperService = TestBed.inject(PaperService);
      authorService = TestBed.inject(AuthorService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Author query and add missing value', () => {
        const paper: IPaper = { id: 456 };
        const authors: IAuthor[] = [{ id: 79275 }];
        paper.authors = authors;

        const authorCollection: IAuthor[] = [{ id: 47601 }];
        jest.spyOn(authorService, 'query').mockReturnValue(of(new HttpResponse({ body: authorCollection })));
        const additionalAuthors = [...authors];
        const expectedCollection: IAuthor[] = [...additionalAuthors, ...authorCollection];
        jest.spyOn(authorService, 'addAuthorToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ paper });
        comp.ngOnInit();

        expect(authorService.query).toHaveBeenCalled();
        expect(authorService.addAuthorToCollectionIfMissing).toHaveBeenCalledWith(authorCollection, ...additionalAuthors);
        expect(comp.authorsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const paper: IPaper = { id: 456 };
        const authors: IAuthor = { id: 30444 };
        paper.authors = [authors];

        activatedRoute.data = of({ paper });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(paper));
        expect(comp.authorsSharedCollection).toContain(authors);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Paper>>();
        const paper = { id: 123 };
        jest.spyOn(paperService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ paper });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: paper }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(paperService.update).toHaveBeenCalledWith(paper);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Paper>>();
        const paper = new Paper();
        jest.spyOn(paperService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ paper });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: paper }));
        saveSubject.complete();

        // THEN
        expect(paperService.create).toHaveBeenCalledWith(paper);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Paper>>();
        const paper = { id: 123 };
        jest.spyOn(paperService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ paper });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(paperService.update).toHaveBeenCalledWith(paper);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackAuthorById', () => {
        it('Should return tracked Author primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackAuthorById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedAuthor', () => {
        it('Should return option if no Author is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedAuthor(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected Author for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedAuthor(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this Author is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedAuthor(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
