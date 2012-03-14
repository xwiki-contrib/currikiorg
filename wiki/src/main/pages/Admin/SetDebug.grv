//<%

// this page should Admin.SetDebug
//                  ==============
import javax.servlet.http.HttpServletRequest
import com.xpn.xwiki.api.XWiki
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.Cookie


//HttpServletRequest request;
//HttpServletResponse response;
//XWiki xwiki;


if("on"==request.turnIt) {
    response.addCookie(new Cookie("cdbg","true"))
    response.sendRedirect(doc.name)
}
if("off"==request.turnIt) {
  response.addCookie(new Cookie("cdbg",""))
  response.sendRedirect(doc.name)
}


println()
println("1 Debug Cookie")
println()

String pref = xwiki.getUserPreferenceFromCookie("cdbg")
//println("Pref is \"" + pref + "\".")
if(""!=pref && null!=pref) {
  println("Debug cookie is on.")
  println();
  println("<a href='" + doc.name + "?turnIt=off'>Turn it off.</a>")
} else {
  println("Debug cookie is off.")
  println();
  println("<a href='" + doc.name + "?turnIt=on'>Turn it on.</a>")
}


%>