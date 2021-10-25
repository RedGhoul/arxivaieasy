import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILikeEntry } from '../like-entry.model';

@Component({
  selector: 'jhi-like-entry-detail',
  templateUrl: './like-entry-detail.component.html',
})
export class LikeEntryDetailComponent implements OnInit {
  likeEntry: ILikeEntry | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ likeEntry }) => {
      this.likeEntry = likeEntry;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
