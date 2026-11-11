import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Equipo } from '../../models/modelos';
import { ApiService } from '../../services/api.service';

/** Paso 1 del flujo: consultar disponibilidad. */
@Component({
  selector: 'app-equipos-disponibles',
  imports: [RouterLink],
  templateUrl: './equipos-disponibles.component.html',
})
export class EquiposDisponiblesComponent implements OnInit {
  equipos: Equipo[] = [];
  error = '';
  cargando = true;

  constructor(protected api: ApiService) {}

  ngOnInit(): void {
    this.api.equiposDisponibles().subscribe({
      next: (equipos) => {
        this.equipos = equipos;
        this.cargando = false;
      },
      error: (e) => {
        this.error = this.api.mensajeDeError(e);
        this.cargando = false;
      },
    });
  }
}
