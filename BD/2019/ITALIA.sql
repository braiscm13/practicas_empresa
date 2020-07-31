CREATE OR REPLACE VIEW CDVDFEMP AS 
  SELECT 
    DFEMP.CIF,
    DFEMP.NOMB,
    DFEMP.IS_DEMO,
    CG_EMPR,
    DOMI,
    DFEMP.CG_PROV,
    DFEMP.CG_MUNI,
    DFEMP.CG_POBL,
    DFEMP.CG_POSTAL,
    TELF,
    FAX,
    CIF_CO,
    F_FIN_HF,
    CL_PROR,
    IDENTIFICADOR,
    REGI_IDEN,
    TIPO_IDEN,
    F_ALTA,
    EMAIL,
    PAG_WEB,
    DFEMP.CG_NACI,
    DIRECCION,
    USUARIO_ALTA,
    F_MODIF,
    USUARIO_MODIF,
    MUNI,
    POBL,
    OBSR,
    TIPO,
    IDGRUPO,
    MOVIL,
    PCONTACTO,
    MCONTACTO,
    LOCALE,
    MAILINFORMEANALISIS,
    PAIS.DSCR    AS PAIS,
    PAIS.PREFIJO AS PREFIJO,
    COND_AUTOM,
    VEH_AUTOM,
    IS_COOPERATIVA,
    DFEMP.CIF_COOPERATIVA,
    CONTRATO_COOP,
    DFEMP.TDI,
    DFEMP.TDI_IP,
    DFEMP.TDI_PORT,
    DFEMP.TDI_USER,
    DFEMP.TDI_PASS,
    DFEMP.TDI_GROUPID,
    DFEMP.GRUPOCLIENTE,
    DFEMP.MOVIL2,
    DFEMP.Z_ERRORES,
    DFEMP.Z_PROCESADO,
    DFEMP.Z_FECHA,
    DFEMP.EMP_FTPSYNC,
    DFEMP.EMP_FTP_URL,
    DFEMP.EMP_FTP_USR,
    DFEMP.EMP_FTP_PAS,
    DFEMP.EMP_FTP_PATH,
	DFEMP.CCC,
    (SELECT COUNT(*) FROM CDVEHICULOS_EMP WHERE CIF = DFEMP.CIF) AS NUM_VEHICLES,
    (SELECT NOMB FROM PROVINCIAS WHERE PROVINCIAS.CG_PROV = DFEMP.CG_PROV) AS PROVINCIA
FROM 
    DFEMP,
    PAIS
WHERE 
    DFEMP.CG_NACI = PAIS.CG_NACI (+);