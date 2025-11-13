# Quick Start Guide for CI/CD Pipeline

## 🎯 For Maintainers

### Initial Setup

1. **Update Repository URLs** in `readme.md`:
   - Replace `YOUR_USERNAME` with your GitHub username
   - Replace `YOUR_REPO_NAME` with your repository name

2. **Push the workflow files** to your repository:
   ```bash
   git add .github/workflows/*.yml
   git add CICD_DOCUMENTATION.md
   git add readme.md
   git add .gitignore
   git commit -m "Add CI/CD pipeline for automated releases"
   git push origin master
   ```

3. **First Release Will Be Created Automatically!**
   - The workflow triggers on push to master
   - It will create a release within 15-20 minutes

## 🔄 How It Works

### Automatic Releases (Recommended)
1. Make your changes in a feature branch
2. Create a pull request to `master`
3. The build test workflow will run automatically
4. After merging to `master`, a release is created automatically

### Version Numbers
- Format: `base-version.build-number`
- Example: `1.0.42` means version 1.0, build 42
- Build number = total commit count
- **No manual versioning needed!**

### What Gets Built
- ✅ Windows `.exe` installer (with JRE 21 bundled)
- ✅ Linux `.deb` package (with JRE 21 bundled)
- ✅ Automatic release notes
- ✅ GitHub Release with download links

## 📋 Release Checklist

Before pushing to master:
- [ ] Test locally with `mvn clean package`
- [ ] Ensure `src/res/icon.png` exists
- [ ] Update any documentation if needed
- [ ] Commit all changes
- [ ] Push to master

After push:
- [ ] Monitor Actions tab for build progress
- [ ] Check release page after ~15-20 minutes
- [ ] Test installers on target platforms
- [ ] Announce release to users

## 🛠️ Troubleshooting

### Build Failed?
1. Go to **Actions** tab
2. Click on the failed workflow run
3. Check the logs for error messages
4. Common issues:
   - Maven build error: Fix code issues
   - Icon missing: Ensure `src/res/icon.png` exists
   - JRE download: Network issue, retry workflow

### No Release Created?
- Check if workflow completed successfully
- Verify GITHUB_TOKEN permissions (automatic, should work)
- Check if tag already exists (workflow will fail)

### Re-run Failed Build
1. Go to **Actions** tab
2. Click on the failed workflow
3. Click **Re-run all jobs** button

## 🎨 Customization

### Change Base Version
Edit `pom.xml`:
```xml
<version>2.0</version>
```
Next release will be `2.0.X`

### Change Release Branch
Edit `.github/workflows/release.yml`:
```yaml
on:
  push:
    branches:
      - main  # or any branch name
```

### Add macOS Build
Add a new job in `release.yml` similar to `build-windows` and `build-linux`

## 📊 Monitoring

### View Build Status
- **Actions Tab**: See all workflow runs
- **README Badges**: Quick status view
- **Email Notifications**: GitHub sends on failure

### Check Release
- **Releases Page**: All published releases
- **Latest Tag**: Points to most recent release
- **Download Stats**: Available in Insights

## 🚀 Advanced Usage

### Manual Release Trigger
1. Go to **Actions** tab
2. Select **Build and Release**
3. Click **Run workflow**
4. Choose branch (usually `master`)
5. Click green **Run workflow** button

### Testing Without Release
- Push to any branch except `master`
- Build test workflow runs automatically
- No release is created
- Useful for testing changes

### Hotfix Release
1. Create hotfix branch from master
2. Make fixes
3. Merge back to master
4. Release is created automatically

## 📦 What Users Get

### Windows Users
- Self-contained `.exe` installer
- ~200-250 MB download
- No Java needed
- Start Menu integration
- Desktop shortcut option
- Easy uninstaller

### Linux Users
- Debian package `.deb`
- ~200-250 MB download
- No Java needed
- Application menu integration
- Install via `dpkg` or Software Center
- Uninstall via package manager

## 🎓 Best Practices

1. **Test Before Merging**: Always create PR and check build test results
2. **Write Good Commit Messages**: They appear in release notes
3. **Tag Important Versions**: Use semantic versioning for major releases
4. **Keep Dependencies Updated**: Regularly update Maven dependencies
5. **Monitor Release Size**: Keep installers under 300 MB if possible

## 📞 Support

- **Documentation**: See `CICD_DOCUMENTATION.md` for detailed info
- **GitHub Issues**: Report pipeline issues
- **Actions Logs**: First place to check for errors

---

**Remember**: Every push to master creates a release. Make sure your code is ready! 🚀

