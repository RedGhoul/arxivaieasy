import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILikeEntry, LikeEntry } from '../like-entry.model';

import { LikeEntryService } from './like-entry.service';

describe('Service Tests', () => {
  describe('LikeEntry Service', () => {
    let service: LikeEntryService;
    let httpMock: HttpTestingController;
    let elemDefault: ILikeEntry;
    let expectedResult: ILikeEntry | ILikeEntry[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(LikeEntryService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        createDate: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            createDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a LikeEntry', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            createDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createDate: currentDate,
          },
          returnedFromService
        );

        service.create(new LikeEntry()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a LikeEntry', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            createDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a LikeEntry', () => {
        const patchObject = Object.assign({}, new LikeEntry());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            createDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of LikeEntry', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            createDate: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a LikeEntry', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addLikeEntryToCollectionIfMissing', () => {
        it('should add a LikeEntry to an empty array', () => {
          const likeEntry: ILikeEntry = { id: 123 };
          expectedResult = service.addLikeEntryToCollectionIfMissing([], likeEntry);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(likeEntry);
        });

        it('should not add a LikeEntry to an array that contains it', () => {
          const likeEntry: ILikeEntry = { id: 123 };
          const likeEntryCollection: ILikeEntry[] = [
            {
              ...likeEntry,
            },
            { id: 456 },
          ];
          expectedResult = service.addLikeEntryToCollectionIfMissing(likeEntryCollection, likeEntry);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a LikeEntry to an array that doesn't contain it", () => {
          const likeEntry: ILikeEntry = { id: 123 };
          const likeEntryCollection: ILikeEntry[] = [{ id: 456 }];
          expectedResult = service.addLikeEntryToCollectionIfMissing(likeEntryCollection, likeEntry);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(likeEntry);
        });

        it('should add only unique LikeEntry to an array', () => {
          const likeEntryArray: ILikeEntry[] = [{ id: 123 }, { id: 456 }, { id: 7932 }];
          const likeEntryCollection: ILikeEntry[] = [{ id: 123 }];
          expectedResult = service.addLikeEntryToCollectionIfMissing(likeEntryCollection, ...likeEntryArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const likeEntry: ILikeEntry = { id: 123 };
          const likeEntry2: ILikeEntry = { id: 456 };
          expectedResult = service.addLikeEntryToCollectionIfMissing([], likeEntry, likeEntry2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(likeEntry);
          expect(expectedResult).toContain(likeEntry2);
        });

        it('should accept null and undefined values', () => {
          const likeEntry: ILikeEntry = { id: 123 };
          expectedResult = service.addLikeEntryToCollectionIfMissing([], null, likeEntry, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(likeEntry);
        });

        it('should return initial array if no LikeEntry is added', () => {
          const likeEntryCollection: ILikeEntry[] = [{ id: 123 }];
          expectedResult = service.addLikeEntryToCollectionIfMissing(likeEntryCollection, undefined, null);
          expect(expectedResult).toEqual(likeEntryCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
