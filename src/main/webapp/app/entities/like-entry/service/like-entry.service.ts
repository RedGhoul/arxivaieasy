import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILikeEntry, getLikeEntryIdentifier } from '../like-entry.model';

export type EntityResponseType = HttpResponse<ILikeEntry>;
export type EntityArrayResponseType = HttpResponse<ILikeEntry[]>;

@Injectable({ providedIn: 'root' })
export class LikeEntryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/like-entries');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(likeEntry: ILikeEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(likeEntry);
    return this.http
      .post<ILikeEntry>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(likeEntry: ILikeEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(likeEntry);
    return this.http
      .put<ILikeEntry>(`${this.resourceUrl}/${getLikeEntryIdentifier(likeEntry) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(likeEntry: ILikeEntry): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(likeEntry);
    return this.http
      .patch<ILikeEntry>(`${this.resourceUrl}/${getLikeEntryIdentifier(likeEntry) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILikeEntry>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILikeEntry[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLikeEntryToCollectionIfMissing(
    likeEntryCollection: ILikeEntry[],
    ...likeEntriesToCheck: (ILikeEntry | null | undefined)[]
  ): ILikeEntry[] {
    const likeEntries: ILikeEntry[] = likeEntriesToCheck.filter(isPresent);
    if (likeEntries.length > 0) {
      const likeEntryCollectionIdentifiers = likeEntryCollection.map(likeEntryItem => getLikeEntryIdentifier(likeEntryItem)!);
      const likeEntriesToAdd = likeEntries.filter(likeEntryItem => {
        const likeEntryIdentifier = getLikeEntryIdentifier(likeEntryItem);
        if (likeEntryIdentifier == null || likeEntryCollectionIdentifiers.includes(likeEntryIdentifier)) {
          return false;
        }
        likeEntryCollectionIdentifiers.push(likeEntryIdentifier);
        return true;
      });
      return [...likeEntriesToAdd, ...likeEntryCollection];
    }
    return likeEntryCollection;
  }

  protected convertDateFromClient(likeEntry: ILikeEntry): ILikeEntry {
    return Object.assign({}, likeEntry, {
      createDate: likeEntry.createDate?.isValid() ? likeEntry.createDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.createDate = res.body.createDate ? dayjs(res.body.createDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((likeEntry: ILikeEntry) => {
        likeEntry.createDate = likeEntry.createDate ? dayjs(likeEntry.createDate) : undefined;
      });
    }
    return res;
  }
}
