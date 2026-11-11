import { Routes } from '@angular/router';
import { EquiposDisponiblesComponent } from './components/equipos-disponibles/equipos-disponibles.component';
import { LoginComponent } from './components/login/login.component';
import { PrestamosComponent } from './components/prestamos/prestamos.component';
import { SolicitudComponent } from './components/solicitud/solicitud.component';
import { sesionGuard } from './guards/sesion.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'equipos', component: EquiposDisponiblesComponent, canActivate: [sesionGuard] },
  { path: 'solicitud/:equipoId', component: SolicitudComponent, canActivate: [sesionGuard] },
  { path: 'prestamos', component: PrestamosComponent, canActivate: [sesionGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'equipos' },
  { path: '**', redirectTo: 'equipos' },
];
