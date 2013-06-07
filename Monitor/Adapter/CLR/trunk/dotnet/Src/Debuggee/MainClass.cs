using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;

namespace Debuggee
{
  class Pet
  {
    public int Age { get; set; }
    public string Name { get; set; }
    public bool Alive { get; set; }
    public Pet Father;
    public int Gender { get; set; }
    public Color Color { get; set; }
  }

  class Color
  {
    public byte R { get; set; }
    public byte G { get; set; }
    public byte B { get; set; }
  }

  class Dog : Pet
  {
    public bool HasDogTag { get; set; }
  }

  class Mouse : Pet
  {
    public bool Safe { get; set; }
  }

  class Cat : Pet
  {
    public bool Sleeping { get; set; }
    public bool Hungry { get; set; }

    public void Eat(Mouse m)
    {
      Debug.Assert(!m.Safe, "UnsafeMouse");
      Debug.Assert(this.Hungry, "CatIsHungry");
      Debug.Assert(!this.Sleeping, "CatIsNotSleeping");
      Debug.Assert(!this.Alive, "CatIsAlive");

      // do some work
      this.Hungry = false;
      m.Alive = false;

      Debug.Assert(!this.Hungry, "CatNotHungry");
      Debug.Assert(!m.Alive, "VictimIsDead");
    }
  }

  class MainClass
  {
    private List<Pet> pets = new List<Pet>();
    private List<Color> colors = new List<Color>();

    [STAThread]
    static void Main(string[] args)
    {
      Console.WriteLine("Running under .NET {0}", Environment.Version);
      Console.WriteLine("My PID: " + Process.GetCurrentProcess().Id.ToString());

      MainClass c = new MainClass();
      c.CreateBasicSnapshot();

      Console.WriteLine("Press enter to exit!");
      Console.Read();
    }

    private void CreateBasicSnapshot()
    {
      Color black = new Color { R = 0, G = 0, B = 0 };
      Color brown = new Color { R = 139, G = 69, B = 19 };
      Color gray = new Color { R = 105, G = 105, B = 105 };
      Color snow = new Color { R = 255, G = 250, B = 250 };

      // Parent cats
      pets.Add(new Cat { Age = 15, Alive = true, Hungry = false, Sleeping = true, Name = "Tom", Gender = 1, Color = black });
      pets.Add(new Cat { Age = 13, Alive = true, Hungry = true, Sleeping = false, Name = "Ada", Gender = 2, Color = black });

       // Child cats
      pets.Add(new Cat { Age = 6, Alive = true, Hungry = false, Sleeping = true, Name = "Felix", Gender = 1, Color = black });
      pets.Add(new Cat { Age = 7, Alive = true, Hungry = false, Sleeping = true, Name = "Bob", Gender = 2, Color = snow });
      pets.Add(new Cat { Age = 8, Alive = true, Hungry = false, Sleeping = false, Name = "Dana", Gender = 1, Color = brown });

      // Dog
      pets.Add(new Dog { Age = 13, Alive = true, Name = "Joe", Gender = 1, Color = brown, HasDogTag = true });

      // Mice
      pets.Add(new Mouse { Age = 9, Alive = true, Name = "Jerry", Gender = 1, Color = gray, Safe = true });
      pets.Add(new Mouse { Age = 5, Alive = true, Name = "Amy", Gender = 2, Color = brown, Safe = false });

      // Cat fatherhood
      Pet father = pets.Single(x => x.Name == "Tom");
      pets.Single(x => x.Name == "Felix").Father = father;
      pets.Single(x => x.Name == "Bob").Father = father;
      pets.Single(x => x.Name == "Dana").Father = father;
    }
  }
}
