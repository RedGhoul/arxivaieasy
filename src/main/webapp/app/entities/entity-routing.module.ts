import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'author',
        data: { pageTitle: 'Authors' },
        loadChildren: () => import('./author/author.module').then(m => m.AuthorModule),
      },
      {
        path: 'paper',
        data: { pageTitle: 'Papers' },
        loadChildren: () => import('./paper/paper.module').then(m => m.PaperModule),
      },
      {
        path: 'subject',
        data: { pageTitle: 'Subjects' },
        loadChildren: () => import('./subject/subject.module').then(m => m.SubjectModule),
      },
      {
        path: 'like-entry',
        data: { pageTitle: 'LikeEntries' },
        loadChildren: () => import('./like-entry/like-entry.module').then(m => m.LikeEntryModule),
      },
      {
        path: 'app-user',
        data: { pageTitle: 'AppUsers' },
        loadChildren: () => import('./app-user/app-user.module').then(m => m.AppUserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
