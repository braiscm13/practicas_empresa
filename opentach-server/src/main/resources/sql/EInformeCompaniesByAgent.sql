select
    cdusu.usuario as agente,
    dfemp.nomb as empresa,
    dfemp.cif,
    prov.NOMB as PROVINCIA,
    (
      select 
        count(*)
      from 
        cdconductores_emp,
        CDVEMPRE_REQ_REALES,
        cdconductor_cont
      where 
        CDVEMPRE_REQ_REALES.CIF = dfemp.cif
        AND CDVEMPRE_REQ_REALES.CG_CONTRATO = cdconductor_cont.CG_CONTRATO
        AND cdconductores_emp.IDCONDUCTOR = cdconductor_cont.IDCONDUCTOR
        AND cdconductores_emp.CIF = dfemp.cif
        AND cdconductor_cont.F_BAJA IS NULL
    ) as num_conductores,
    (
       select 
        count(*)
      from 
        cdvehiculos_emp,
        CDVEMPRE_REQ_REALES,
        cdvehiculo_cont
      where 
        CDVEMPRE_REQ_REALES.CIF = dfemp.cif
        AND CDVEMPRE_REQ_REALES.CG_CONTRATO = cdvehiculo_cont.CG_CONTRATO
        AND cdvehiculos_emp.MATRICULA = cdvehiculo_cont.MATRICULA
        AND cdvehiculos_emp.CIF = dfemp.cif
        AND cdvehiculo_cont.F_BAJA IS NULL
    )as num_vehiculos
from
  cdusu,
  cdusu_dfemp,
  dfemp,
  PROVINCIAS prov,
  CDEMPRE_REQ req
where 
    cdusu.usuario  =cdusu_dfemp.usuario
    and cdusu_dfemp.cif =dfemp.cif
    and cdusu.NIVEL_CD = (select nivel_cd from cdniveles where dscr = 'AGENTE')
    and dfemp.CG_PROV = prov.CG_PROV(+)
    and dfemp.CIF = req.CIF
    #WHERE#
order by cdusu.usuario, dfemp.nomb