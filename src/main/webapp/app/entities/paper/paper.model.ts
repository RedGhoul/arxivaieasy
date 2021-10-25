import * as dayjs from 'dayjs';
import { IAuthor } from 'app/entities/author/author.model';
import { ILikeEntry } from 'app/entities/like-entry/like-entry.model';

export interface IPaper {
  id?: number;
  title?: string;
  abstractText?: string;
  submitedDate?: dayjs.Dayjs | null;
  createdDate?: dayjs.Dayjs | null;
  pdfLink?: string;
  baseLink?: string;
  authors?: IAuthor[] | null;
  likeEntries?: ILikeEntry[] | null;
}

export class Paper implements IPaper {
  constructor(
    public id?: number,
    public title?: string,
    public abstractText?: string,
    public submitedDate?: dayjs.Dayjs | null,
    public createdDate?: dayjs.Dayjs | null,
    public pdfLink?: string,
    public baseLink?: string,
    public authors?: IAuthor[] | null,
    public likeEntries?: ILikeEntry[] | null
  ) {}
}

export function getPaperIdentifier(paper: IPaper): number | undefined {
  return paper.id;
}
