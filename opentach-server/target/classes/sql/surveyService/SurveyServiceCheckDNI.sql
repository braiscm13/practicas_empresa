SELECT idconductor, idpersonal, dni
FROM 
(SELECT idconductor, null as idpersonal, dni
FROM cdconductores_emp
union all
SELECT null as idconductor, idpersonal AS idpersonal, dni
FROM cdpersonal_emp) a
WHERE substr(dni, 0, LENGTH(dni) - 1) = ?