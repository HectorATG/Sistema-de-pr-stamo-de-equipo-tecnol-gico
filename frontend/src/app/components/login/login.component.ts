import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  cargando = false;

  constructor(
    private api: ApiService,
    private router: Router,
  ) {}

  entrar(): void {
    this.error = '';
    this.cargando = true;
    this.api.login(this.username.trim(), this.password).subscribe({
      next: (usuario) => {
        this.cargando = false;
        this.router.navigate([usuario.rol === 'RESPONSABLE' ? '/prestamos' : '/equipos']);
      },
      error: (e) => {
        this.cargando = false;
        this.error = this.api.mensajeDeError(e);
      },
    });
  }
}
