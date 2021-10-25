import * as dayjs from 'dayjs';
import { IAppUser } from 'app/entities/app-user/app-user.model';
import { IPaper } from 'app/entities/paper/paper.model';

export interface ILikeEntry {
  id?: number;
  createDate?: dayjs.Dayjs | null;
  appUsers?: IAppUser[] | null;
  papers?: IPaper[] | null;
}

export class LikeEntry implements ILikeEntry {
  constructor(
    public id?: number,
    public createDate?: dayjs.Dayjs | null,
    public appUsers?: IAppUser[] | null,
    public papers?: IPaper[] | null
  ) {}
}

export function getLikeEntryIdentifier(likeEntry: ILikeEntry): number | undefined {
  return likeEntry.id;
}
