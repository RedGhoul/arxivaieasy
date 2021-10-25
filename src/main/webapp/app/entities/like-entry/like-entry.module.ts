import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LikeEntryComponent } from './list/like-entry.component';
import { LikeEntryDetailComponent } from './detail/like-entry-detail.component';
import { LikeEntryUpdateComponent } from './update/like-entry-update.component';
import { LikeEntryDeleteDialogComponent } from './delete/like-entry-delete-dialog.component';
import { LikeEntryRoutingModule } from './route/like-entry-routing.module';

@NgModule({
  imports: [SharedModule, LikeEntryRoutingModule],
  declarations: [LikeEntryComponent, LikeEntryDetailComponent, LikeEntryUpdateComponent, LikeEntryDeleteDialogComponent],
  entryComponents: [LikeEntryDeleteDialogComponent],
})
export class LikeEntryModule {}
