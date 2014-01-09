package org.curriki.xwiki.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/** A small servlet-filter so that the skin is chosen
 * from the session.
  */
public class SkinChoiceFilter implements Filter {

    Pattern mobileUserAgent = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        // ignored for now
        // mobileUserAgent = Pattern.compile("iphone|ipad|ipod|android|blackberry|mini|windows\\sce|palm");
    }

    public void destroy() {
    }

    static List<String> autoRespurrikiPages = (List<String>) Arrays.asList(

            "/view/Coll_Group_CurrikiGeometry",
            "/view/Coll_CurrikiGeometryStudents",
            "/view/Coll_Group_SampleCurrikiGeometryCourse"
            //,
            //"/view/Coll_Group_GeometryBetaTesters/",
            //"/view/Coll_Group_CurrikiGeometry",
            //"/view/Courses/Geometry"
            );
    static String skinChoiceForceRespurriki = null;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest) {
            HttpServletRequest hrq = (HttpServletRequest) request;
            String skinFromParameter = null;// ignored currently; hrq.getParameter("skin");
            String tempSkin = hrq.getParameter("tempskin");


            HttpSession session = hrq.getSession(false);

            // need to replace list? if yes do so now

            if(session!=null) {
                String skinChoiceFromAtt = (String) session.getServletContext().getAttribute("SkinChoiceForceRespurriki");
                if(skinChoiceFromAtt != null && !skinChoiceFromAtt.equals(skinChoiceFromAtt)) {
                    autoRespurrikiPages = Arrays.asList(skinChoiceFromAtt.split(" |\\t|\\r|\\n"));
                }

            }

            if(skinFromParameter==null && tempSkin== null &&
                    autoRespurrikiPages!=null && !autoRespurrikiPages.isEmpty()) {
                String path = hrq.getPathInfo();
                for(String pageSubString: autoRespurrikiPages) {
                    System.out.println("Checking " + pageSubString + " in " + path );
                    if(path.contains(pageSubString)) {
                        tempSkin = "respurriki";
                        break;
                    }
                }
            }

            if(session!=null) {
                if(skinFromParameter !=null) {
                    System.out.println("Settings skin: " + skinFromParameter);
                    session.setAttribute("skin", skinFromParameter);
                } else { // no skinFromParameter
                    String mySkin = (String) session.getAttribute("skin");
                    if(tempSkin!=null) mySkin = tempSkin;
                    /* String path = hrq.getRequestURL().toString();
                String referer = hrq.getHeader("Referer");
                if(referer==null) referer = ""; */
                    /* // Potential to allow device selection within an xwiki page. A bit risky
                } else if("GET".equals((hrq.getMethod())) &&
                        !path.contains("Util/ChooseSkin") &&
                        !referer.contains("Util/ChooseSkin")){
                    // mySkin is null, need to filter by devices: send to ChooseSkin
                    ((HttpServletResponse) response).sendRedirect("/xwiki/bin/view/Util/ChooseSkin?xredirect=" + URLEncoder.encode(path));
                    request = null;
                    */
                    if(mySkin==null) {
                        String userAgent = hrq.getHeader("User-Agent");
                        if(userAgent!=null && mobileUserAgent !=null) {
                            if(mobileUserAgent.matcher(userAgent.toLowerCase()).find()) {
                                mySkin = "respurriki";
                                session.setAttribute("skin", mySkin);
                            }
                        }
                    }
                    final String theSkin = mySkin;
                    // System.out.println("Skin should be: " + mySkin);
                    if(mySkin!=null) {
                        // rework params so that the skin is included
                        HttpServletRequestWrapper hrq2 = new HttpServletRequestWrapper(hrq) {
                            @Override
                            public String getParameter(String name) {
                                if("skin".equals(name)) { return theSkin; }
                                else return super.getParameter(name);
                            }
                        };
                        request = hrq2;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

}
