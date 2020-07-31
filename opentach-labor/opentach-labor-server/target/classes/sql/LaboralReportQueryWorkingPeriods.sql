SELECT 
	PT.TPPERIODO,
	PT.FECINI,
	PT.NUM_TARJ
FROM 
	CDPERIODOS_TRABAJO PT 
WHERE	 
	PT.NUMREQ = ?
	AND PT.IDCONDUCTOR = ?
	AND PT.FECINI>=?
	AND PT.FECINI<=?
ORDER BY 
	PT.FECINI