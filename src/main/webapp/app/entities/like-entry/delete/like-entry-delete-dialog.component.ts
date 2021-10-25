import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILikeEntry } from '../like-entry.model';
import { LikeEntryService } from '../service/like-entry.service';

@Component({
  templateUrl: './like-entry-delete-dialog.component.html',
})
export class LikeEntryDeleteDialogComponent {
  likeEntry?: ILikeEntry;

  constructor(protected likeEntryService: LikeEntryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.likeEntryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
