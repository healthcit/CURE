<%--
Copyright (c) 2013 HealthCare It, Inc.
All rights reserved. This program and the accompanying materials
are made available under the terms of the BSD 3-Clause license
which accompanies this distribution, and is available at
http://directory.fsf.org/wiki/License:BSD_3Clause

Contributors:
    HealthCare It, Inc - initial API and implementation
--%>
<%@page isThreadSafe="true" %>
<% 
	try{
		int duration = (request.getParameter("interval")!=null ? Integer.parseInt(request.getParameter("interval")) : 10000);
		Thread.sleep(duration);
   	} catch(InterruptedException ex){} 
%>
<%= "yes" %>
