import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AccionPrestamo, Equipo, Prestamo, SolicitudRequest, Usuario } from '../models/modelos';

const API = 'http://localhost:8080/api';
const CLAVE_SESION = 'prestamos.sesion';

interface Sesion {
  token: string; // credenciales en Base64 para HTTP Basic
  usuario: Usuario;
}

/**
 * Único punto de comunicación con el backend (componente "ApiService" del
 * diagrama de componentes). Agrega la cabecera HTTP Basic a cada petición.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private sesion: Sesion | null = this.leerSesion();
  readonly usuario = signal<Usuario | null>(this.sesion?.usuario ?? null);

  constructor(private http: HttpClient) {}

  // ---------- Autenticación ----------

  login(username: string, password: string): Observable<Usuario> {
    const token = btoa(`${username}:${password}`);
    const headers = new HttpHeaders({ Authorization: `Basic ${token}` });
    return this.http.get<Usuario>(`${API}/auth/me`, { headers }).pipe(
      tap((usuario) => {
        this.sesion = { token, usuario };
        this.usuario.set(usuario);
        try {
          sessionStorage.setItem(CLAVE_SESION, JSON.stringify(this.sesion));
        } catch {
          /* sin almacenamiento: la sesión vive solo en memoria */
        }
      }),
    );
  }

  logout(): void {
    this.sesion = null;
    this.usuario.set(null);
    try {
      sessionStorage.removeItem(CLAVE_SESION);
    } catch {
      /* nada que limpiar */
    }
  }

  // ---------- Equipos ----------

  equiposDisponibles(): Observable<Equipo[]> {
    return this.http.get<Equipo[]>(`${API}/equipos?estado=DISPONIBLE`, this.opciones());
  }

  equipos(): Observable<Equipo[]> {
    return this.http.get<Equipo[]>(`${API}/equipos`, this.opciones());
  }

  // ---------- Préstamos ----------

  solicitar(solicitud: SolicitudRequest): Observable<Prestamo> {
    return this.http.post<Prestamo>(`${API}/prestamos`, solicitud, this.opciones());
  }

  prestamos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${API}/prestamos`, this.opciones());
  }

  misPrestamos(): Observable<Prestamo[]> {
    return this.http.get<Prestamo[]>(`${API}/prestamos/mios`, this.opciones());
  }

  cambiarEstado(id: number, accion: AccionPrestamo): Observable<Prestamo> {
    return this.http.put<Prestamo>(`${API}/prestamos/${id}/${accion}`, null, this.opciones());
  }

  /** Convierte un error HTTP en un mensaje legible para la pantalla. */
  mensajeDeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor (¿está corriendo en el puerto 8080?).';
    }
    if (error.status === 401) {
      return 'Usuario o contraseña incorrectos.';
    }
    if (error.status === 403) {
      return 'Tu rol no tiene permiso para esta operación.';
    }
    return error.error?.mensaje ?? `Error ${error.status}`;
  }

  private opciones(): { headers: HttpHeaders } {
    return { headers: new HttpHeaders({ Authorization: `Basic ${this.sesion?.token ?? ''}` }) };
  }

  private leerSesion(): Sesion | null {
    try {
      const texto = sessionStorage.getItem(CLAVE_SESION);
      return texto ? (JSON.parse(texto) as Sesion) : null;
    } catch {
      return null;
    }
  }
}
