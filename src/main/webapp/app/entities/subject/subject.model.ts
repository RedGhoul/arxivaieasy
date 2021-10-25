export interface ISubject {
  id?: number;
  title?: string;
}

export class Subject implements ISubject {
  constructor(public id?: number, public title?: string) {}
}

export function getSubjectIdentifier(subject: ISubject): number | undefined {
  return subject.id;
}
