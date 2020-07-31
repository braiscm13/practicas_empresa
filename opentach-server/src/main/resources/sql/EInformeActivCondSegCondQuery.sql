SELECT 
	CDCONDUCTORES_EMP.NOMBRE ||' '|| CDCONDUCTORES_EMP.APELLIDOS as nombre 
FROM  
	CDACTIVIDADES B, 
	CDCONDUCTORES_EMP 
where  
	CDCONDUCTORES_EMP.IDCONDUCTOR  = B.IDCONDUCTOR 
	AND	B.idconductor<>'XXXXXXXXX' 
	AND b.matricula = ?
	AND B.tpactividad = 4 
	AND b.matricula <> '???????????' 
	AND B.ranura = 0 
	AND B.fec_comienzo <= ?
	AND B.fec_fin >= ?