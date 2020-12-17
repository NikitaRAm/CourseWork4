using Newtonsoft.Json;
using Romanovich.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Security.Claims;
using System.Web.Http;

namespace Romanovich.Controllers
{
    public class UserController : ApiController
    {

        public class Users
        {
            public string UserName { get; set; }
            public string Password { get; set; }
            public string Email { get; set; }
            public int isAdmin { get; set; }
            public string Image { get; set; }
        };

        [HttpPost]
        public HttpResponseMessage CreateUser([FromBody] Users user)
        {
            RomanovichEntities db = new RomanovichEntities();
            if (db.Users.FirstOrDefault(x => x.UserName == user.UserName) == null && db.Users.FirstOrDefault(x => x.Email == user.Email) == null)
            {
                db.AddUser(user.UserName, user.Email, user.Password, user.isAdmin, user.Image);
                return new HttpResponseMessage(HttpStatusCode.OK);
            }
            else
            {
                return new HttpResponseMessage(HttpStatusCode.MovedPermanently);
            }
        }
        
        [Authorize]
        [HttpGet]
        public string GetUsers()
        {
            RomanovichEntities db = new RomanovichEntities();
            ClaimsPrincipal principal = Request.GetRequestContext().Principal as ClaimsPrincipal;
            var Name = ClaimsPrincipal.Current.Identity.Name;
            db.Configuration.LazyLoadingEnabled = false;
            var res = db.Users.ToList();
            var toDelete = db.Users.FirstOrDefault(x => x.UserName == Name);
            res.Remove(toDelete);
            return JsonConvert.SerializeObject(res);
        }

    }
}
