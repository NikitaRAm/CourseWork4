using Microsoft.Owin.Security;
using Microsoft.Owin.Security.OAuth;
using Romanovich.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Web;

namespace Romanovich.Providers
{
    public class OAuthCustomTokenProvider : OAuthAuthorizationServerProvider
    {
        public override System.Threading.Tasks.Task GrantResourceOwnerCredentials(OAuthGrantResourceOwnerCredentialsContext context)
        {
            return System.Threading.Tasks.Task.Factory.StartNew(() =>
            {
                String UserRole;
                RomanovichEntities db = new RomanovichEntities();

                var email = context.UserName;
                var password = context.Password;
                var userService = new UserService();
                var user = userService.Validate(email, password);
                if (user != null)
                {
                    var claims = new List<Claim>()
                {
                    new Claim(ClaimTypes.Sid, Convert.ToString(user.id)),
                    new Claim(ClaimTypes.Name, user.UserName)
                };

                    if (user.IsAdmin == 1)
                    {

                        UserRole = "Admin";
                    }
                    else
                    {
                        UserRole = "User";
                    }

                    claims.Add(new Claim(ClaimTypes.Role, UserRole));
                    var data = new Dictionary<string, string>
                {
                    { "userName", user.UserName },
                    { "roles", string.Join(",", UserRole)}
                };
                    var properties = new AuthenticationProperties(data);

                    ClaimsIdentity oAuthIdentity = new ClaimsIdentity(claims,
                        Startup.OAuthOptions.AuthenticationType);

                    var ticket = new AuthenticationTicket(oAuthIdentity, properties);
                    context.Validated(ticket);
                }
                else
                {
                    context.SetError("invalid_grant", "Either email or password is incorrect");
                }

            });
        }

        public override System.Threading.Tasks.Task ValidateClientAuthentication(OAuthValidateClientAuthenticationContext context)
        {
            if (context.ClientId == null)
                context.Validated();

            return System.Threading.Tasks.Task.FromResult<object>(null);
        }


        public override System.Threading.Tasks.Task TokenEndpoint(OAuthTokenEndpointContext context)
        {
            foreach (KeyValuePair<string, string> property in context.Properties.Dictionary)
            {
                context.AdditionalResponseParameters.Add(property.Key, property.Value);
            }
            return System.Threading.Tasks.Task.FromResult<object>(null);
        }

    }
}