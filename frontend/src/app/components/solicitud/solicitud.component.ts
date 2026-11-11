import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Equipo } from '../../models/modelos';
import { ApiService } from '../../services/api.service';

/** Paso 2 del flujo: solicitar el préstamo de un equipo. */
@Component({
  selector: 'app-solicitud',
  imports: [FormsModule, RouterLink],
  templateUrl: './solicitud.component.html',
  styleUrl: './solicitud.component.css',
})
export class SolicitudComponent implements OnInit {
  equipoId = 0;
  equipo: Equipo | null = null;
  fechaInicio = this.hoy();
  fechaFin = this.hoy();
  error = '';
  enviando = false;

  constructor(
    private api: ApiService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.equipoId = Number(this.route.snapshot.paramMap.get('equipoId'));
    this.api.equiposDisponibles().subscribe({
      next: (equipos) => {
        this.equipo = equipos.find((e) => e.id === this.equipoId) ?? null;
        if (!this.equipo) {
          this.error = 'El equipo ya no está disponible.';
        }
      },
      error: (e) => (this.error = this.api.mensajeDeError(e)),
    });
  }

  enviar(): void {
    this.error = '';
    this.enviando = true;
    this.api
      .solicitar({ equipoId: this.equipoId, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin })
      .subscribe({
        next: () => this.router.navigate(['/prestamos']),
        error: (e) => {
          this.enviando = false;
          this.error = this.api.mensajeDeError(e);
        },
      });
  }

  private hoy(): string {
    const d = new Date();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${mes}-${dia}`;
  }
}
