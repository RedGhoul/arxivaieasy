import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';
import { ILikeEntry } from 'app/entities/like-entry/like-entry.model';

export interface IAppUser {
  id?: number;
  bio?: string | null;
  createdDate?: dayjs.Dayjs | null;
  user?: IUser | null;
  likeEntries?: ILikeEntry[] | null;
}

export class AppUser implements IAppUser {
  constructor(
    public id?: number,
    public bio?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public user?: IUser | null,
    public likeEntries?: ILikeEntry[] | null
  ) {}
}

export function getAppUserIdentifier(appUser: IAppUser): number | undefined {
  return appUser.id;
}
