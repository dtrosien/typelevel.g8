package $package$

import cats.effect.IO
import pureconfig.*

final case class AppConfig(name: String, greeting: String) derives ConfigReader

object AppConfig:
  /** Loads the `app` section of `application.conf` (and any overrides from system properties). */
  def load: IO[AppConfig] = IO(ConfigSource.default.at("app").loadOrThrow[AppConfig])
