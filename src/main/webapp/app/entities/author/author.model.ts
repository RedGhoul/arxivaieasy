import { IPaper } from 'app/entities/paper/paper.model';

export interface IAuthor {
  id?: number;
  name?: string;
  historyLink?: string;
  papers?: IPaper[] | null;
}

export class Author implements IAuthor {
  constructor(public id?: number, public name?: string, public historyLink?: string, public papers?: IPaper[] | null) {}
}

export function getAuthorIdentifier(author: IAuthor): number | undefined {
  return author.id;
}
