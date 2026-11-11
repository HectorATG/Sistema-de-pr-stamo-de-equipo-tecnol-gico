export type Rol = 'SOLICITANTE' | 'RESPONSABLE' | 'ADMIN';
export type EstadoEquipo = 'DISPONIBLE' | 'PRESTADO';
export type TipoEquipo = 'LAPTOP' | 'PROYECTOR' | 'KIT';
export type EstadoPrestamo = 'SOLICITADO' | 'AUTORIZADO' | 'RECHAZADO' | 'ENTREGADO' | 'DEVUELTO';

export interface Usuario {
  id: number;
  nombre: string;
  username: string;
  rol: Rol;
}

export interface Equipo {
  id: number;
  nombre: string;
  tipo: TipoEquipo;
  numeroSerie: string;
  estado: EstadoEquipo;
}

export interface Prestamo {
  id: number;
  solicitante: string;
  solicitanteUsername: string;
  equipoId: number;
  equipo: string;
  tipoEquipo: TipoEquipo;
  estadoEquipo: EstadoEquipo;
  fechaSolicitud: string;
  fechaInicio: string;
  fechaFin: string;
  fechaEntrega: string | null;
  fechaDevolucion: string | null;
  estado: EstadoPrestamo;
}

export interface SolicitudRequest {
  equipoId: number;
  fechaInicio: string;
  fechaFin: string;
}

export type AccionPrestamo = 'autorizar' | 'rechazar' | 'entregar' | 'devolver';
