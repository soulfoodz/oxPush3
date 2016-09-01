#
# Be sure to run `pod lib lint ox-push2-ios-pod.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'ox-push2-ios-pod'
  s.version          = '0.1.1'
  s.summary          = 'A short description of ox-push2-ios-pod.'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  s.homepage         = 'https://github.com/GluuFederation/ox-push2-ios-pod.git'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'NazarYavornytskyy' => 'nazaryavornytskyy@gmail.com' }
  s.source           = { :git => 'https://github.com/GluuFederation/ox-push2-ios-pod.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '8.0'

  s.source_files = 'ox-push2-ios-pod/Classes/**/*.{m,h,mm,hpp,cpp,c}'
  
  #s.resource_bundles = {
  #   'Resources' => ['ox-push2-ios-pod/Resources/**/*.{xcdatamodeld,png,jpeg,jpg}']
  #}

  # s.public_header_files = 'Pod/Classes/**/*.h'
  # s.frameworks = 'UIKit', 'MapKit'
  s.resources = 'ox-push2-ios-pod/Classes/DataStore/Database/ecs.xcdatamodeld'
  s.dependency 'AFNetworking'
  s.dependency 'NHNetworkTime'
  s.dependency 'NSHash'

end
