using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading;

namespace Debuggee
{
  interface NotADog
  {
    bool LikesDogs { get; set; }
  }

  abstract class Pet
  {
    private readonly int NUM_OF_CHILDS = 10;
    public int Age { get; set; }
    public string Name { get; set; }
    public Pet Parent { get; set; }
    public Pet[] ArrayChildren;

    public Pet()
    {
      this.ArrayChildren = new Pet[NUM_OF_CHILDS];
    }

    public void SayName() { Console.WriteLine(this.Name); }
  }

  public sealed class PetColor
  {
    public byte R { get; set; }
    public byte G { get; set; }
    public byte B { get; set; }
  }

  class Dog : Pet
  {
    public bool HasLicence { get; set; }
    public PetColor Color { get; set; }
  }

  class Cat : Pet, NotADog
  {
    public bool IsSleeping { get; set; }
    public bool LikesDogs { get; set; }
  }

  class MainClass
  {
    private readonly int MAX_PETS = 8;
    private readonly int TIME_SPAN = 5000;
    private List<Pet> Pets = new List<Pet>();

    [STAThread]
    static void Main(string[] args)
    {
      Console.WriteLine("Running under .NET {0}", Environment.Version);
      Console.WriteLine("My PID: " + Process.GetCurrentProcess().Id.ToString());

      MainClass c = new MainClass();
      c.DoSomething();
    }

    private void DoSomething()
    {
      bool associationCreated = false;

      while (true)
      {
        CreateSomePets();

        if (!associationCreated)
        {
          Pets.First().ArrayChildren[0] = Pets.Last();
          Pets.Last().Parent = Pets.First();
          associationCreated = true;
        }

        PrintName(Pets.Last());
        Thread.Sleep(TIME_SPAN);
      }
    }

    private void CreateSomePets()
    {
      if (Pets.Count >= MAX_PETS)
        return;

      Pets.Add(new Cat { Age = Pets.Count + 1, Name = "Cat_" + (Pets.Count + 1).ToString("0000").PadLeft(Pets.Count + 1, '0'), IsSleeping = true });
      Pets.Add(new Cat { Age = Pets.Count + 1, Name = "Cat_" + (Pets.Count + 1).ToString("0000").PadLeft(Pets.Count + 1, '0') });
      Pets.Add(new Dog { Age = Pets.Count + 1, Name = "Dog_" + (Pets.Count + 1).ToString("0000").PadLeft(Pets.Count + 1, '0'), HasLicence = true });
      Pets.Add(new Dog { Age = Pets.Count + 1, Name = "Dog_" + (Pets.Count + 1).ToString("0000").PadLeft(Pets.Count + 1, '0'), Color = new PetColor { R = 10, G = 10, B = 10 } });
    }

    static private void PrintName(Pet pet)
    {
      pet.SayName();
    }
  }
}
