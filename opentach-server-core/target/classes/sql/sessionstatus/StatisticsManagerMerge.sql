MERGE INTO CDLOGSESIONSTAT log using 
	CDLOGSESIONSTAT_TEMP logtemp  on (
			log.USUARIO = logtemp.USUARIO
			and log.F_INI = logtemp.F_INI
			and log.MANAGER = logtemp.MANAGER
			and log.FORM = logtemp.FORM
			and log.ACTION = logtemp.ACTION
		)
		when not matched then 
			insert (log.USUARIO, log.F_INI, log.MANAGER, log.FORM, log.ACTION, log.CLICKS)
			values (logtemp.USUARIO, logtemp.F_INI,logtemp.MANAGER,logtemp.FORM,logtemp.ACTION, logtemp.CLICKS)
		when matched then
			update set log.CLICKS=log.CLICKS+logtemp.CLICKS