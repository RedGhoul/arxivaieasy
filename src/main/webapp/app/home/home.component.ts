import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { IPaper } from '../entities/paper/paper.model';
import { PaperService } from '../entities/paper/service/paper.service';
import { HttpResponse } from '@angular/common/http';
import { ITEMS_PER_PAGE } from '../config/pagination.constants';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  papers: IPaper[] | null | undefined;
  isLoading = false;
  search = '';
  page: number;
  itemsPerPage: number;

  constructor(private router: Router, private paperService: PaperService) {
    this.page = 0;
    this.itemsPerPage = ITEMS_PER_PAGE;
  }

  ngOnInit(): void {
    this.paperService
      .findByText(this.search, {
        page: this.page,
        size: this.itemsPerPage,
      })
      .subscribe(
        (res: HttpResponse<IPaper[]>) => {
          this.papers = res.body;
          this.isLoading = false;
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  findByText(): void {
    this.paperService
      .findByText(this.search, {
        page: this.page,
        size: this.itemsPerPage,
      })
      .subscribe(
        (res: HttpResponse<IPaper[]>) => {
          this.papers = res.body;
          this.isLoading = false;
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  nextPage(): void {
    this.page = this.page + 1;
    this.paperService
      .findByText(this.search, {
        page: this.page + 1,
        size: this.itemsPerPage,
      })
      .subscribe(
        (res: HttpResponse<IPaper[]>) => {
          this.papers = res.body;
          this.isLoading = false;
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  prevPage(): void {
    this.page = this.page - 1;
    this.paperService
      .findByText(this.search, {
        page: this.page - 1,
        size: this.itemsPerPage,
      })
      .subscribe(
        (res: HttpResponse<IPaper[]>) => {
          this.papers = res.body;
          this.isLoading = false;
        },
        () => {
          this.isLoading = false;
        }
      );
  }
}
