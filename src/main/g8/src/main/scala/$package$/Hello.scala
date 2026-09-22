package $package$

import cats.effect.*

object Hello extends IOApp.Simple:
  def run = AppConfig.load.flatMap(config => IO.println(config.greeting))
