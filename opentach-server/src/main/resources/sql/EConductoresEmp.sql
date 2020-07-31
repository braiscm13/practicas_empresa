select 
empre_cond.DNI,
empre_cond.NOMBRE,
empre_cond.APELLIDOS,
empre_cond.IDCONDUCTOR,
empre_cond.EXTERNAL_EMPLOYEE_ID,
MAX(F_DESCARGA_DATOS) AS F_DESCARGA_DATOS
from 
( select cdconductores_emp.DNI,
cdconductores_emp.NOMBRE,
cdconductores_emp.APELLIDOS,
cdconductores_emp.IDCONDUCTOR,
cdconductores_emp.EXTERNAL_EMPLOYEE_ID,
cdconductores_emp.cif,
CDVEMPRE_REQ_REALES.CG_CONTRATO
from cdconductores_emp,  CDVEMPRE_REQ_REALES
where
CDVEMPRE_REQ_REALES.CIF = cdconductores_emp.cif 
) empre_cond, (
SELECT  CDFICHEROS.IDORIGEN,CDFICHEROS_CONTRATO.CG_CONTRATO, MAX(CDFICHEROS.F_DESCARGA_DATOS) AS F_DESCARGA_DATOS
FROM  CDFICHEROS, CDFICHEROS_CONTRATO
WHERE CDFICHEROS.IDFICHERO = CDFICHEROS_CONTRATO.IDFICHERO 
GROUP BY CDFICHEROS.IDORIGEN,CDFICHEROS_CONTRATO.CG_CONTRATO)FICHEROS
where 
empre_cond.IDCONDUCTOR = FICHEROS.IDORIGEN(+) 
AND empre_cond.CG_CONTRATO = FICHEROS.CG_CONTRATO(+)
and empre_cond.cif = ? and empre_cond.idconductor= ?
GROUP BY empre_cond.DNI,
empre_cond.NOMBRE,
empre_cond.APELLIDOS,
empre_cond.IDCONDUCTOR,
empre_cond.EXTERNAL_EMPLOYEE_ID
