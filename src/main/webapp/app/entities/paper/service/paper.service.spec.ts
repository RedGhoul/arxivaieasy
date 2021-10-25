import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPaper, Paper } from '../paper.model';

import { PaperService } from './paper.service';

describe('Service Tests', () => {
  describe('Paper Service', () => {
    let service: PaperService;
    let httpMock: HttpTestingController;
    let elemDefault: IPaper;
    let expectedResult: IPaper | IPaper[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PaperService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        title: 'AAAAAAA',
        abstractText: 'AAAAAAA',
        submitedDate: currentDate,
        createdDate: currentDate,
        pdfLink: 'AAAAAAA',
        baseLink: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            submitedDate: currentDate.format(DATE_FORMAT),
            createdDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Paper', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            submitedDate: currentDate.format(DATE_FORMAT),
            createdDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            submitedDate: currentDate,
            createdDate: currentDate,
          },
          returnedFromService
        );

        service.create(new Paper()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Paper', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            title: 'BBBBBB',
            abstractText: 'BBBBBB',
            submitedDate: currentDate.format(DATE_FORMAT),
            createdDate: currentDate.format(DATE_FORMAT),
            pdfLink: 'BBBBBB',
            baseLink: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            submitedDate: currentDate,
            createdDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Paper', () => {
        const patchObject = Object.assign(
          {
            abstractText: 'BBBBBB',
            pdfLink: 'BBBBBB',
          },
          new Paper()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            submitedDate: currentDate,
            createdDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Paper', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            title: 'BBBBBB',
            abstractText: 'BBBBBB',
            submitedDate: currentDate.format(DATE_FORMAT),
            createdDate: currentDate.format(DATE_FORMAT),
            pdfLink: 'BBBBBB',
            baseLink: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            submitedDate: currentDate,
            createdDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Paper', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPaperToCollectionIfMissing', () => {
        it('should add a Paper to an empty array', () => {
          const paper: IPaper = { id: 123 };
          expectedResult = service.addPaperToCollectionIfMissing([], paper);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(paper);
        });

        it('should not add a Paper to an array that contains it', () => {
          const paper: IPaper = { id: 123 };
          const paperCollection: IPaper[] = [
            {
              ...paper,
            },
            { id: 456 },
          ];
          expectedResult = service.addPaperToCollectionIfMissing(paperCollection, paper);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Paper to an array that doesn't contain it", () => {
          const paper: IPaper = { id: 123 };
          const paperCollection: IPaper[] = [{ id: 456 }];
          expectedResult = service.addPaperToCollectionIfMissing(paperCollection, paper);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(paper);
        });

        it('should add only unique Paper to an array', () => {
          const paperArray: IPaper[] = [{ id: 123 }, { id: 456 }, { id: 10073 }];
          const paperCollection: IPaper[] = [{ id: 123 }];
          expectedResult = service.addPaperToCollectionIfMissing(paperCollection, ...paperArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const paper: IPaper = { id: 123 };
          const paper2: IPaper = { id: 456 };
          expectedResult = service.addPaperToCollectionIfMissing([], paper, paper2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(paper);
          expect(expectedResult).toContain(paper2);
        });

        it('should accept null and undefined values', () => {
          const paper: IPaper = { id: 123 };
          expectedResult = service.addPaperToCollectionIfMissing([], null, paper, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(paper);
        });

        it('should return initial array if no Paper is added', () => {
          const paperCollection: IPaper[] = [{ id: 123 }];
          expectedResult = service.addPaperToCollectionIfMissing(paperCollection, undefined, null);
          expect(expectedResult).toEqual(paperCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
