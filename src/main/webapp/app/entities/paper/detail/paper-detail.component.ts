import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPaper } from '../paper.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-paper-detail',
  templateUrl: './paper-detail.component.html',
})
export class PaperDetailComponent implements OnInit {
  paper: IPaper | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paper }) => {
      this.paper = paper;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
