import { Component, OnInit } from '@angular/core';
import { AccionPrestamo, Prestamo } from '../../models/modelos';
import { ApiService } from '../../services/api.service';

/**
 * Pasos 3 a 6 del flujo. El responsable autoriza (el backend valida la
 * disponibilidad), registra entrega y devolución; ambos roles consultan
 * el estado y el historial de los préstamos.
 */
@Component({
  selector: 'app-prestamos',
  templateUrl: './prestamos.component.html',
})
export class PrestamosComponent implements OnInit {
  prestamos: Prestamo[] = [];
  error = '';
  exito = '';
  cargando = true;
  enProceso: number | null = null;

  constructor(protected api: ApiService) {}

  get esResponsable(): boolean {
    return this.api.usuario()?.rol === 'RESPONSABLE';
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    const consulta = this.esResponsable ? this.api.prestamos() : this.api.misPrestamos();
    consulta.subscribe({
      next: (prestamos) => {
        this.prestamos = prestamos;
        this.cargando = false;
      },
      error: (e) => {
        this.error = this.api.mensajeDeError(e);
        this.cargando = false;
      },
    });
  }

  cambiar(prestamo: Prestamo, accion: AccionPrestamo): void {
    this.error = '';
    this.exito = '';
    this.enProceso = prestamo.id;
    this.api.cambiarEstado(prestamo.id, accion).subscribe({
      next: (actualizado) => {
        this.enProceso = null;
        this.exito = `Préstamo #${actualizado.id}: ${actualizado.estado}. Equipo ${actualizado.estadoEquipo}.`;
        this.cargar();
      },
      error: (e) => {
        this.enProceso = null;
        this.error = this.api.mensajeDeError(e);
      },
    });
  }
}
