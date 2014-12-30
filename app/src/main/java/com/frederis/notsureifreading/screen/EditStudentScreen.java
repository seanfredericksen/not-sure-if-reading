package com.frederis.notsureifreading.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.frederis.notsureifreading.MainBlueprint;
import com.frederis.notsureifreading.MainScope;
import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.actionbar.ToolbarOwner;
import com.frederis.notsureifreading.model.Student;
import com.frederis.notsureifreading.model.Students;
import com.frederis.notsureifreading.presenter.ActivityResultListener;
import com.frederis.notsureifreading.presenter.ActivityResultRegistrar;
import com.frederis.notsureifreading.util.StartActivityForResultHandler;
import com.frederis.notsureifreading.util.TitledBlueprint;
import com.frederis.notsureifreading.view.EditStudentView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Singleton;

import dagger.Provides;
import flow.Flow;
import flow.HasParent;
import flow.Layout;
import mortar.MortarScope;
import mortar.ViewPresenter;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

@Layout(R.layout.edit_student_view)
public class EditStudentScreen implements HasParent<StudentsListScreen>, TitledBlueprint {

    private final long mStudentId;

    public EditStudentScreen(long studentId) {
        mStudentId = studentId;
    }

    @Override
    public String getMortarScopeName() {
        return "Edit Student Screen {" + "studentId = " + mStudentId + '}';
    }

    @Override
    public Object getDaggerModule() {
        return new Module();
    }

    @Override
    public StudentsListScreen getParent() {
        return new StudentsListScreen();
    }

    @dagger.Module(injects = EditStudentView.class, addsTo = MainBlueprint.Module.class)
    public class Module {

        @Provides
        Observable<Student> provideStudent(Students students) {
            return students.getStudent(mStudentId);
        }

        @Provides
        Presenter providePresenter(Students students,
                                   Observable<Student> student,
                                   @MainScope Flow flow,
                                   ToolbarOwner actionBar,
                                   StartActivityForResultHandler imageHandler,
                                   ActivityResultRegistrar activityResultRegistrar) {
            return new Presenter(mStudentId, students, student, flow, actionBar, imageHandler, activityResultRegistrar);
        }

    }

    @Singleton
    public static class Presenter extends ViewPresenter<EditStudentView> implements ActivityResultListener {

        private static final int REQUEST_CAPTURE_STUDENT_IMAGE = 1123;
        private static final String KEY_IMAGE_CAPTURE_URI = "imageCaptureUri";

        private final long studentId;
        private final Students students;
        private final Observable<Student> student;
        private final Flow flow;
        private final ToolbarOwner actionBar;
        private final StartActivityForResultHandler activityStarter;
        private final ActivityResultRegistrar activityResultRegistrar;

        private Uri mImageCaptureUri;

        private final Subject<String, String> name = BehaviorSubject.create();
        private final Subject<String, String> startingWord = BehaviorSubject.create();
        private final Subject<String, String> endingWord = BehaviorSubject.create();
        private final Subject<Uri, Uri> image = BehaviorSubject.create();

        public Presenter(long studentId,
                         Students students,
                         Observable<Student> student,
                         Flow flow,
                         ToolbarOwner actionBar,
                         StartActivityForResultHandler activityStarter,
                         ActivityResultRegistrar activityResultRegistrar) {
            this.studentId = studentId;
            this.students = students;
            this.student = student;
            this.flow = flow;
            this.actionBar = actionBar;
            this.activityStarter = activityStarter;
            this.activityResultRegistrar = activityResultRegistrar;
        }

        private Observable<Long> createStudentIdObservable() {
            return student.map(new Func1<Student, Long>() {
                @Override
                public Long call(Student student) {
                    return student.getId();
                }
            });
        }

        private Observable<String> createNameObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return student.getName();
                        }
                    });
        }

        private Observable<String> createStartingWordObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return String.valueOf(student.getStartingWord());
                        }
                    });
        }

        private Observable<String> createEndingWordObservable() {
            return student
                    .map(new Func1<Student, String>() {
                        @Override
                        public String call(Student student) {
                            return String.valueOf(student.getEndingWord());
                        }
                    });
        }

        private Observable<Uri> createStudentImageObservable() {
            return student
                    .map(new Func1<Student, Uri>() {
                        @Override
                        public Uri call(Student student) {
                            return student.getImageUri();
                        }
                    });
        }

        public void exit() {
            flow.goBack();
        }

        public void updateOrInsertStudent(String name,
                                          long startingWord,
                                          long endingWord) {
            students.updateOrInsertStudent(new Student(studentId, name, mImageCaptureUri != null ? mImageCaptureUri : Student.IMAGE_UNCHANGED, startingWord, endingWord));
        }

        public void captureImage() {
            Intent intent = getTakePictureIntent();

            if (intent != null) {
                activityStarter.startActivityForResult(getTakePictureIntent(), REQUEST_CAPTURE_STUDENT_IMAGE);
            } else {
                //Display error finding camera message
            }
        }

        private Intent getTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getView().getContext().getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mImageCaptureUri = Uri.fromFile(photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    return takePictureIntent;
                }
            }

            return null;
        }

        private File createImageFile() throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_" + studentId + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            return image;
        }


        public void assessStudent() {
            flow.goTo(new PerformAssessmentScreen(studentId));
        }

        @Override public void dropView(EditStudentView view) {
            super.dropView(view);
        }

        @Override
        protected void onSave(Bundle outState) {
            super.onSave(outState);

            outState.putParcelable(KEY_IMAGE_CAPTURE_URI, mImageCaptureUri);
        }

        @Override public void onLoad(Bundle savedInstanceState) {
            final EditStudentView v = getView();
            if (v == null) return;

            super.onLoad(savedInstanceState);
            if (savedInstanceState != null) {
                mImageCaptureUri = savedInstanceState.getParcelable(KEY_IMAGE_CAPTURE_URI);
            }

            ToolbarOwner.Config actionBarConfig = actionBar.getConfig();

            actionBarConfig = actionBarConfig.withElevationDimension(R.dimen.no_elevation);

            actionBar.setConfig(actionBarConfig);

            createNameObservable().subscribe(name);
            createStartingWordObservable().subscribe(startingWord);
            createEndingWordObservable().subscribe(endingWord);

            if (mImageCaptureUri != null) {
                Observable.just(mImageCaptureUri).subscribe(image);
            } else {
                createStudentImageObservable().subscribe(image);
            }

            v.populateImage(image);
            v.populateName(name);
            v.populateStartingWord(startingWord);
            v.populateEndingWord(endingWord);
        }

        @Override
        protected void onEnterScope(MortarScope scope) {
            super.onEnterScope(scope);

            activityResultRegistrar.register(scope, this);
        }

        @Override
        public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
            final EditStudentView v = getView();

            if (v != null && requestCode == REQUEST_CAPTURE_STUDENT_IMAGE && mImageCaptureUri != null) {
                v.populateImage(Observable.just(mImageCaptureUri));
                return true;
            }

            return false;
        }
    }

    @Override
    public CharSequence getTitle() {
        return "";
    }

}