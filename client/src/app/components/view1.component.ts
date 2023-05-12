import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {firstValueFrom } from 'rxjs'

@Component({
  selector: 'app-view1',
  templateUrl: './view1.component.html',
  styleUrls: ['./view1.component.css']
})
export class View1Component implements OnInit {

  @ViewChild('zipFile') zipFile!: ElementRef // get reference to input element using its name 
  form!: FormGroup

  constructor(private fb: FormBuilder, private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.form = this.createForm(); 
  }

  createForm(): FormGroup {
    return this.fb.group({
      name: this.fb.control('', [Validators.required]),
      title: this.fb.control('', [Validators.required]),
      comments: this.fb.control(''),
      archive: this.fb.control('', [Validators.required])
    })
  }

  onSubmit() {
    const formData = new FormData(); 
    formData.set('name', this.form.value['name'])
    formData.set('title', this.form.value['title'])
    formData.set('comments', this.form.get('comments')?.value == null ? 'null' : this.form.value['comments'])
    formData.set('file', this.zipFile.nativeElement.files[0])
    console.info('>>> form data', formData)

    firstValueFrom ( this.http.post('https://adamant-taste-production.up.railway.app/upload', formData))
      .then( (result: any) => {
        console.log(result)

        // error 500 -> display error message in popup dialog box
        if ( result.status === 500) {
          const errorJson = JSON.parse(result.body)
          alert(errorJson.error); 
        } else {
          // created 201, receive bundleId
          const bundleId = result.bundleId
          console.info('>>> bundle Id: ', bundleId)
          this.router.navigate(['/display', bundleId])
        }
      })
  }

}
