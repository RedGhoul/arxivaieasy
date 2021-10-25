import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISubject } from '../subject.model';
import { SubjectService } from '../service/subject.service';

@Component({
  templateUrl: './subject-delete-dialog.component.html',
})
export class SubjectDeleteDialogComponent {
  subject?: ISubject;

  constructor(protected subjectService: SubjectService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.subjectService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
