import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { lastValueFrom } from 'rxjs';
import { Bundle } from '../models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-view0',
  templateUrl: './view0.component.html',
  styleUrls: ['./view0.component.css']
})
export class View0Component implements OnInit {

  bundles!: Bundle[]
  bundlesLoaded: boolean = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    
    lastValueFrom (this.http.get('https://adamant-taste-production.up.railway.app/bundles')).then( (result: any) => {
      console.info('>>> Result: ', result)
      this.bundlesLoaded = true;
    })
  }

}
