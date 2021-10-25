import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SubjectComponent } from './list/subject.component';
import { SubjectDetailComponent } from './detail/subject-detail.component';
import { SubjectUpdateComponent } from './update/subject-update.component';
import { SubjectDeleteDialogComponent } from './delete/subject-delete-dialog.component';
import { SubjectRoutingModule } from './route/subject-routing.module';

@NgModule({
  imports: [SharedModule, SubjectRoutingModule],
  declarations: [SubjectComponent, SubjectDetailComponent, SubjectUpdateComponent, SubjectDeleteDialogComponent],
  entryComponents: [SubjectDeleteDialogComponent],
})
export class SubjectModule {}
