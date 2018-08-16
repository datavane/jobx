<template>
  <main class="main">
    <section class="content">
      <div class="content__inner">
        <header class="content__title">
          <h1>Form Elements</h1>

          <div class="actions">
            <a href="" class="actions__item zmdi zmdi-trending-up"></a>
            <a href="" class="actions__item zmdi zmdi-check-all"></a>

            <div class="dropdown actions__item">
              <i data-toggle="dropdown" class="zmdi zmdi-more-vert"></i>
              <div class="dropdown-menu dropdown-menu-right">
                <a href="" class="dropdown-item">Refresh</a>
                <a href="" class="dropdown-item">Manage Widgets</a>
                <a href="" class="dropdown-item">Settings</a>
              </div>
            </div>
          </div>
        </header>

        <div class="card">
          <div class="card-body">
            <h4 class="card-title">Textual inputs</h4>
            <h6 class="card-subtitle">Basic Textual inputs with different sizes by height and column.</h6>

            <h3 class="card-body__title">Control sizing</h3>

            <div class="form-group">
              <input type="text" class="form-control form-control-sm" placeholder="Input Small">
              <i class="form-group__bar"></i>
            </div>

            <div class="form-group">
              <input type="text" class="form-control" placeholder="Input Default">
              <i class="form-group__bar"></i>
            </div>

            <div class="form-group">
              <input type="text" class="form-control form-control-lg" placeholder="Input Large">
              <i class="form-group__bar"></i>
            </div>

            <br>

            <h3 class="card-body__title">Column sizing</h3>

            <div class="row">
              <div class="col-sm-4">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-4">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <div class="col-sm-4">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-4">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <div class="col-sm-4">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-4">
                  <i class="form-group__bar"></i>
                </div>
              </div>

              <div class="col-sm-3">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-3">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <div class="col-sm-3">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-3">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <div class="col-sm-3">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-3">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <div class="col-sm-3">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-3">
                  <i class="form-group__bar"></i>
                </div>
              </div>

              <div class="col-sm-6">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-6">
                  <i class="form-group__bar"></i>
                </div>
              </div>
              <div class="col-sm-6">
                <div class="form-group">
                  <input type="text" class="form-control" placeholder="col-sm-6">
                  <i class="form-group__bar"></i>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <h4 class="card-title">Textarea</h4>
            <h6 class="card-subtitle">Form control which supports multiple lines of text. Change 'rows' attribute as
              necessary.</h6>

            <div class="form-group">
              <textarea class="form-control" rows="5" placeholder="Let us type some lorem ipsum...."></textarea>
              <i class="form-group__bar"></i>
            </div>

            <br>

            <h3 class="card-body__title">Height Auto Growing</h3>

            <div class="form-group">
              <textarea class="form-control textarea-autosize"
                        placeholder="Start pressing Enter to see growing..."></textarea>
              <i class="form-group__bar"></i>
            </div>

            <br>

            <h3 class="card-body__title">Disabled State</h3>

            <div class="form-group">
              <textarea class="form-control" disabled>This is disabled</textarea>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <h4 class="card-title">Select</h4>
            <h6 class="card-subtitle">Use the default option, or add multiple to show multiple options at once.</h6>

            <div class="row">
              <div class="col-sm-6">
                <h3 class="card-body__title">Basic Example</h3>

                <div class="form-group">
                  <div class="select">
                    <select class="form-control">
                      <option>Select an Option</option>
                      <option>Option 1</option>
                      <option>Option 2</option>
                      <option>Option 3</option>
                      <option>Option 4</option>
                      <option>Option 5</option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="col-sm-6">
                <h3 class="card-body__title">Disabled Stat</h3>

                <div class="form-group form-group--select">
                  <div class="select">
                    <select class="form-control" disabled>
                      <option>Select an Option</option>
                      <option>Option 1</option>
                      <option>Option 2</option>
                      <option>Option 3</option>
                      <option>Option 4</option>
                      <option>Option 5</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card">
          <div class="card-body">
            <h4 class="card-title">Checkbox and Radio</h4>
            <h6 class="card-subtitle">Default checkboxes and radios are improved upon with the help of
              <code>.form-check</code>, a single class for both input types that improves the layout and behavior of
              their HTML elements. Checkboxes are for selecting one or several options in a list, while radios are for
              selecting one option from many.</h6>

            <h3 class="card-body__title">Default (stacked)</h3>
            <p>By default, any number of checkboxes and radios that are immediate sibling will be vertically stacked and
              appropriately spaced with <code>.form-check</code>.</p>

            <br>

            <label class="custom-control custom-checkbox">
              <input type="checkbox" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span
                class="custom-control-description">Option one is this and that-be sure to include why it's great</span>
            </label>

            <div class="clearfix mb-2"></div>

            <label class="custom-control custom-checkbox">
              <input type="checkbox" class="custom-control-input" disabled>
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Option two is disabled</span>
            </label>

            <br/>
            <br/>

            <label class="custom-control custom-radio">
              <input id="radio1" name="radio" type="radio" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Toggle this custom radio</span>
            </label>

            <div class="clearfix mb-2"></div>

            <label class="custom-control custom-radio">
              <input type="radio" name="radio" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Or toggle this other custom radio</span>
            </label>

            <div class="clearfix mb-2"></div>

            <label class="custom-control custom-radio">
              <input type="radio" name="radio" class="custom-control-input" disabled>
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">This one is disabled</span>
            </label>

            <br/>
            <br/>
            <br/>

            <h3 class="card-body__title">Inline Checkboxes and Radios</h3>

            <label class="custom-control custom-checkbox">
              <input type="checkbox" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">One</span>
            </label>

            <label class="custom-control custom-checkbox">
              <input type="checkbox" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Two</span>
            </label>

            <label class="custom-control custom-checkbox">
              <input type="checkbox" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Three</span>
            </label>

            <div class="clearfix mb-2"></div>

            <label class="custom-control custom-radio">
              <input type="radio" name="radio-inline" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">One</span>
            </label>

            <label class="custom-control custom-radio">
              <input type="radio" name="radio-inline" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Two</span>
            </label>

            <label class="custom-control custom-radio">
              <input type="radio" name="radio-inline" class="custom-control-input">
              <span class="custom-control-indicator"></span>
              <span class="custom-control-description">Three</span>
            </label>
          </div>
        </div>
      </div>

      <footer class="footer hidden-xs-down">
        <p>© Super Admin Responsive. All rights reserved.</p>
        <ul class="nav footer__nav">
          <a class="nav-link" href="">Homepage</a>
          <a class="nav-link" href="">Company</a>
          <a class="nav-link" href="">Support</a>
          <a class="nav-link" href="">News</a>
          <a class="nav-link" href="">Contacts</a>
        </ul>
      </footer>
    </section>
  </main>
</template>

<script type="text/ecmascript-6">
  export default {}
</script>

<style scoped>

</style>
