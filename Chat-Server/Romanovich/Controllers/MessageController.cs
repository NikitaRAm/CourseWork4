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
    public class MessageController : ApiController
    {
        public class Message
        {
            public string text { get; set; }
            public string reciever { get; set; }
            public string image { get; set; }
        };

        public class ExtendedMessage
        {
            public int id { get; set; }
            public int IdSender { get; set; }
            public int IdReceiver { get; set; }
            public string Text { get; set; }
            public string Image { get; set; }
            public DateTime Date { get; set; }
        };

        [HttpPost]
        [Authorize]
        public HttpResponseMessage SendMessage([FromBody]Message message)
        {
            RomanovichEntities db = new RomanovichEntities();
            ClaimsPrincipal principal = Request.GetRequestContext().Principal as ClaimsPrincipal;
            var Name = ClaimsPrincipal.Current.Identity.Name;
            var sender = db.Users.FirstOrDefault(x => x.UserName == Name).id;
            var reciever = db.Users.FirstOrDefault(x => x.UserName == message.reciever).id;
            DateTime date = DateTime.Now;
            db.SendMessage(sender, reciever, message.text, message.image, date);
            return new HttpResponseMessage(HttpStatusCode.OK);
        }

        [HttpGet]
        [Authorize]
        public string GetMyMessage(string fr)
        {
            RomanovichEntities db = new RomanovichEntities();
            db.Configuration.LazyLoadingEnabled = false;
            ClaimsPrincipal principal = Request.GetRequestContext().Principal as ClaimsPrincipal;
            var Name = ClaimsPrincipal.Current.Identity.Name;
            var im = db.Users.FirstOrDefault(x => x.UserName == Name).id;
            var hs = db.Users.FirstOrDefault(x => x.UserName == fr);
            var messages = db.Messages.Where(x => (x.IdSender == im  && x.IdReceiver == hs.id) || (x.IdSender == hs.id && x.IdReceiver == im)).OrderBy(x => x.Date).ToList();

            List<ExtendedMessage> normalizedMessages = new List<ExtendedMessage>();
            messages.ForEach(value => normalizedMessages.Add(new ExtendedMessage { id = value.id, IdSender = value.IdSender, IdReceiver = value.IdReceiver, Text = value.Message, Date = value.Date, Image = value.Image }));
            return JsonConvert.SerializeObject(normalizedMessages);
        }
    }
}
