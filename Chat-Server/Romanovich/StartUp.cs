using Microsoft.Owin;
using Microsoft.Owin.Security.OAuth;
using Owin;
using Romanovich.Providers;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Romanovich
{
    public class Startup
    {
        public static OAuthAuthorizationServerOptions OAuthOptions { get; private set; }

        public void Configuration(IAppBuilder app)
        {
            OAuthOptions = new OAuthAuthorizationServerOptions
            {
                TokenEndpointPath = new PathString("/token"),
                Provider = new OAuthCustomTokenProvider(), 
                AccessTokenExpireTimeSpan = TimeSpan.FromHours(2),
                AllowInsecureHttp = true,
                RefreshTokenProvider = new OAuthCustomRefreshTokenProvider() 
            };
            app.UseOAuthBearerTokens(OAuthOptions);
        }
    }
}