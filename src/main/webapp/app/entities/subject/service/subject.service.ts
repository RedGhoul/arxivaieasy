import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISubject, getSubjectIdentifier } from '../subject.model';

export type EntityResponseType = HttpResponse<ISubject>;
export type EntityArrayResponseType = HttpResponse<ISubject[]>;

@Injectable({ providedIn: 'root' })
export class SubjectService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/subjects');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(subject: ISubject): Observable<EntityResponseType> {
    return this.http.post<ISubject>(this.resourceUrl, subject, { observe: 'response' });
  }

  update(subject: ISubject): Observable<EntityResponseType> {
    return this.http.put<ISubject>(`${this.resourceUrl}/${getSubjectIdentifier(subject) as number}`, subject, { observe: 'response' });
  }

  partialUpdate(subject: ISubject): Observable<EntityResponseType> {
    return this.http.patch<ISubject>(`${this.resourceUrl}/${getSubjectIdentifier(subject) as number}`, subject, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISubject>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISubject[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSubjectToCollectionIfMissing(subjectCollection: ISubject[], ...subjectsToCheck: (ISubject | null | undefined)[]): ISubject[] {
    const subjects: ISubject[] = subjectsToCheck.filter(isPresent);
    if (subjects.length > 0) {
      const subjectCollectionIdentifiers = subjectCollection.map(subjectItem => getSubjectIdentifier(subjectItem)!);
      const subjectsToAdd = subjects.filter(subjectItem => {
        const subjectIdentifier = getSubjectIdentifier(subjectItem);
        if (subjectIdentifier == null || subjectCollectionIdentifiers.includes(subjectIdentifier)) {
          return false;
        }
        subjectCollectionIdentifiers.push(subjectIdentifier);
        return true;
      });
      return [...subjectsToAdd, ...subjectCollection];
    }
    return subjectCollection;
  }
}
