import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PaperComponent } from './list/paper.component';
import { PaperDetailComponent } from './detail/paper-detail.component';
import { PaperUpdateComponent } from './update/paper-update.component';
import { PaperDeleteDialogComponent } from './delete/paper-delete-dialog.component';
import { PaperRoutingModule } from './route/paper-routing.module';

@NgModule({
  imports: [SharedModule, PaperRoutingModule],
  declarations: [PaperComponent, PaperDetailComponent, PaperUpdateComponent, PaperDeleteDialogComponent],
  entryComponents: [PaperDeleteDialogComponent],
})
export class PaperModule {}
