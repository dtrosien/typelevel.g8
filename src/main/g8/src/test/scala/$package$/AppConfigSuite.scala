package $package$

import weaver.*

object AppConfigSuite extends SimpleIOSuite:

  test("application.conf is loaded into AppConfig") {
    AppConfig.load.map { cfg =>
      expect(cfg.name == "$name$") and expect(cfg.greeting == "Hello toolkit!")
    }
  }
