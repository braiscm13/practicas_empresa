SELECT 
  CG_CONTRATO , 
  DNI , 
  NUMREQ , 
  IDCONDUCTOR , 
  APELLIDOS , 
  NOMBRE , 
  MATRICULA , 
  DSCR , 
  FECINI , 
  FECFIN , 
  MINUTOS , 
  KM_INI , 
  KM_FIN , 
  KM_REC , 
  PROCEDENCIA , 
  ORIGEN , 
  DESTINO , 
  DIA , 
  DIA_SEM , 
  SEMANA , 
  MES , 
  MES2 , 
  HORA , 
  VEL_MED , 
  ANHO , 
  NUM_TRJ_CONDU , 
  HORAS , 
  INI_SEMANA , 
  FIN_SEMANA, 
  DIA_ANUAL
FROM 
  CDVINFORM_USOVEHICULO_TEMP
WHERE 
  CG_CONTRATO = ? 
  #IDCONDUCTOR# 
  AND  FECINI >= ?
  AND FECINI <= ?    
ORDER BY 
  idconductor,
  FECINI