import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaper } from '../paper.model';
import { PaperService } from '../service/paper.service';

@Component({
  templateUrl: './paper-delete-dialog.component.html',
})
export class PaperDeleteDialogComponent {
  paper?: IPaper;

  constructor(protected paperService: PaperService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paperService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
