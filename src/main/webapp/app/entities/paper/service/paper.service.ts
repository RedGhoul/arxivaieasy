import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPaper, getPaperIdentifier } from '../paper.model';

export type EntityResponseType = HttpResponse<IPaper>;
export type EntityArrayResponseType = HttpResponse<IPaper[]>;

@Injectable({ providedIn: 'root' })
export class PaperService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/papers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(paper: IPaper): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paper);
    return this.http
      .post<IPaper>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(paper: IPaper): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paper);
    return this.http
      .put<IPaper>(`${this.resourceUrl}/${getPaperIdentifier(paper) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(paper: IPaper): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paper);
    return this.http
      .patch<IPaper>(`${this.resourceUrl}/${getPaperIdentifier(paper) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPaper>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  findPublic(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPaper>(`${this.resourceUrl}/public/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  findByText(search: string, req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPaper[]>(`${this.resourceUrl}/public?find=${search}`, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPaper[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPaperToCollectionIfMissing(paperCollection: IPaper[], ...papersToCheck: (IPaper | null | undefined)[]): IPaper[] {
    const papers: IPaper[] = papersToCheck.filter(isPresent);
    if (papers.length > 0) {
      const paperCollectionIdentifiers = paperCollection.map(paperItem => getPaperIdentifier(paperItem)!);
      const papersToAdd = papers.filter(paperItem => {
        const paperIdentifier = getPaperIdentifier(paperItem);
        if (paperIdentifier == null || paperCollectionIdentifiers.includes(paperIdentifier)) {
          return false;
        }
        paperCollectionIdentifiers.push(paperIdentifier);
        return true;
      });
      return [...papersToAdd, ...paperCollection];
    }
    return paperCollection;
  }

  protected convertDateFromClient(paper: IPaper): IPaper {
    return Object.assign({}, paper, {
      submitedDate: paper.submitedDate?.isValid() ? paper.submitedDate.format(DATE_FORMAT) : undefined,
      createdDate: paper.createdDate?.isValid() ? paper.createdDate.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.submitedDate = res.body.submitedDate ? dayjs(res.body.submitedDate) : undefined;
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((paper: IPaper) => {
        paper.submitedDate = paper.submitedDate ? dayjs(paper.submitedDate) : undefined;
        paper.createdDate = paper.createdDate ? dayjs(paper.createdDate) : undefined;
      });
    }
    return res;
  }
}
