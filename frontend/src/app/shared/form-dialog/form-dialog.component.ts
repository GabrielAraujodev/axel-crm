import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export interface FieldDef {
  key: string;
  label: string;
  type: 'text' | 'email' | 'number' | 'date' | 'datetime-local' | 'select' | 'textarea' | 'checkbox' | 'password';
  required?: boolean;
  options?: { value: any; label: string }[];
}

export interface FormDialogData {
  title: string;
  fields: FieldDef[];
  entity?: any;
}

@Component({
  selector: 'app-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatIconModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './form-dialog.component.html',
  styleUrl: './form-dialog.component.scss',
})
export class FormDialogComponent {
  form: FormGroup;
  title: string;
  fields: FieldDef[];
  isEdit: boolean;
  saving = false;

  constructor(
    private dialogRef: MatDialogRef<FormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FormDialogData,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {
    this.title = data.title;
    this.fields = data.fields;
    this.isEdit = !!data.entity;

    const controls: Record<string, FormControl> = {};
    for (const f of this.fields) {
      const value = data.entity?.[f.key] ?? (f.type === 'checkbox' ? false : '');
      controls[f.key] = new FormControl(value, f.required ? Validators.required : undefined);
    }
    this.form = new FormGroup(controls);

    // Dynamic stage loading based on selected pipeline
    const pipelineControl = this.form.get('pipelineId');
    if (pipelineControl) {
      pipelineControl.valueChanges.subscribe(pipelineId => {
        if (!pipelineId) return;
        this.http.get<any>(`${environment.apiUrl}/pipelines/${pipelineId}/stages`).subscribe({
          next: (stages: any) => {
            const content = Array.isArray(stages) ? stages : (stages.content || []);
            const stageField = this.fields.find(f => f.key === 'stageId');
            if (stageField) {
              stageField.options = content.map((s: any) => ({ value: s.id, label: s.name }));
              const currentVal = this.form.get('stageId')?.value;
              if (!content.some((s: any) => s.id === currentVal)) {
                this.form.get('stageId')?.setValue('');
              }
            }
          }
        });
      });
    }
  }

  submit(): void {
    if (this.form.invalid) return;
    this.dialogRef.close(this.form.value);
  }

  cancel(): void {
    this.dialogRef.close(null);
  }

  generateAI(key: string): void {
    const userPrompt = prompt('Digite as diretrizes para a IA (ex: "escreva uma análise técnica das trincas na parede de gesso"):');
    if (!userPrompt) return;

    this.snackBar.open('Gerando texto profissional com IA Gemini...', 'Aguarde', { duration: 2000 });
    
    this.http.post<any>(`${environment.apiUrl}/integrations/ai/generate`, {
      prompt: userPrompt,
      context: this.form.get(key)?.value || ''
    }).subscribe({
      next: (res) => {
        this.form.get(key)?.setValue(res.text);
        this.snackBar.open('Texto gerado com sucesso!', 'OK', { duration: 3000 });
      },
      error: () => {
        this.snackBar.open('Erro ao conectar com assistente de IA', 'OK', { duration: 3000 });
      }
    });
  }
}
