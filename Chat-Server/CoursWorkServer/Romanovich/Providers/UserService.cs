
using Romanovich.Models;
using System.Collections.Generic;
using System.Linq;

namespace Romanovich.Providers
{
    public class UserService
    {
        RomanovichEntities db = new RomanovichEntities();

        public Users Validate(string email, string password)
       => db.Users.FirstOrDefault(x => x.Email == email && x.Password == password);

        public List<Users> GetUserList()
        {
            return db.Users.OrderBy(x => x.UserName).ToList();
        }
    }
}